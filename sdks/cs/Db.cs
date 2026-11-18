using System;
using System.Diagnostics;
using System.IO;

namespace B4n1.Db
{
    /// <summary>
    /// Thin C# wrapper over the <c>b4n1-db</c> standalone binary. All storage,
    /// SQL, graph, vector and audit logic lives in the binary; this class only
    /// invokes it. JSON is returned as text so the wrapper stays dependency-free.
    /// </summary>
    public sealed class Database
    {
        public const string Version = "0.1.0";

        private const int TimeoutMs = 60000;

        private readonly string _path;

        public Database(string path = "./data")
        {
            _path = path;
        }

        /// <summary>
        /// Platform → file name inside the package's <c>native/</c> dir. Must
        /// match the names scripts/bundle-sdk-binaries.sh writes (one list,
        /// four readers).
        /// </summary>
        private static readonly (string Rid, string File)[] Bundled =
        {
            ("linux-x64", "b4n1-db-linux-amd64"),
            ("linux-arm64", "b4n1-db-linux-arm64"),
            ("osx-x64", "b4n1-db-macos-x64"),
            ("osx-arm64", "b4n1-db-macos-arm64"),
            ("win-x64", "b4n1-db-windows-amd64.exe"),
        };

        /// <summary>Runtime identifier of the current process.</summary>
        private static string CurrentRid()
        {
            var arch = System.Runtime.InteropServices.RuntimeInformation.OSArchitecture
                == System.Runtime.InteropServices.Architecture.Arm64 ? "arm64" : "x64";
            if (System.Runtime.InteropServices.RuntimeInformation.IsOSPlatform(
                    System.Runtime.InteropServices.OSPlatform.Windows))
            {
                return "win-" + arch;
            }

            if (System.Runtime.InteropServices.RuntimeInformation.IsOSPlatform(
                    System.Runtime.InteropServices.OSPlatform.OSX))
            {
                return "osx-" + arch;
            }

            return "linux-" + arch;
        }

        /// <summary>Path to the binary shipped with the package, or null.</summary>
        private static string? BundledBinary()
        {
            var rid = CurrentRid();
            foreach (var entry in Bundled)
            {
                if (entry.Rid != rid)
                {
                    continue;
                }

                var candidate = Path.Combine(AppContext.BaseDirectory, "native", entry.File);
                return File.Exists(candidate) ? candidate : null;
            }

            return null;
        }

        /// <summary>
        /// Resolves the binary: <c>B4N1_DB_BIN</c>, then the copy bundled in
        /// this package, then <c>PATH</c>.
        /// </summary>
        public static string Binary()
        {
            var env = Environment.GetEnvironmentVariable("B4N1_DB_BIN");
            if (!string.IsNullOrEmpty(env) && File.Exists(env))
            {
                return env;
            }

            var bundled = BundledBinary();
            if (bundled != null)
            {
                return bundled;
            }

            var pathEnv = Environment.GetEnvironmentVariable("PATH") ?? string.Empty;
            foreach (var dir in pathEnv.Split(Path.PathSeparator))
            {
                if (string.IsNullOrEmpty(dir))
                {
                    continue;
                }

                var candidate = Path.Combine(dir, "b4n1-db");
                if (File.Exists(candidate))
                {
                    return candidate;
                }
            }

            throw new InvalidOperationException(
                "b4n1-db binary not found; set B4N1_DB_BIN or install the b4n1-db binary");
        }

        /// <summary>Database counters as JSON.</summary>
        public string Stats() => Run(true, "stats");

        /// <summary>Writes a key/value pair.</summary>
        public void Put(string key, string value) => Run(false, "kv", "put", key, value);

        /// <summary>Reads a key; returns the entry as JSON.</summary>
        public string Get(string key) => Run(true, "kv", "get", key);

        /// <summary>Runs the offline one-click demonstration and returns its transcript.</summary>
        public string Demo() => Run(false, "demo");

        private string Run(bool jsonOut, params string[] args)
        {
            var start = new ProcessStartInfo
            {
                FileName = Binary(),
                RedirectStandardOutput = true,
                RedirectStandardError = true,
                UseShellExecute = false,
            };
            start.ArgumentList.Add("--db");
            start.ArgumentList.Add(_path);
            if (jsonOut)
            {
                start.ArgumentList.Add("--json");
            }

            foreach (var arg in args)
            {
                start.ArgumentList.Add(arg);
            }

            using (var process = Process.Start(start))
            {
                if (process == null)
                {
                    throw new InvalidOperationException("failed to start b4n1-db");
                }

                var stdout = process.StandardOutput.ReadToEnd();
                var stderr = process.StandardError.ReadToEnd();
                if (!process.WaitForExit(TimeoutMs))
                {
                    process.Kill();
                    throw new TimeoutException("b4n1-db timed out after " + TimeoutMs + " ms");
                }

                if (process.ExitCode != 0)
                {
                    throw new InvalidOperationException(
                        string.IsNullOrWhiteSpace(stderr) ? stdout.Trim() : stderr.Trim());
                }

                return stdout;
            }
        }
    }
}
