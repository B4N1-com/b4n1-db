namespace B4n1.Db.Engines
{
    /// <summary>Queue engine: <c>b4n1-db queue &lt;verb&gt;</c>.</summary>
    public sealed class QueueEngine : Engine
    {
        internal QueueEngine(Database db) : base(db) { }

        /// <summary>Appends a message and returns its id.</summary>
        public string Push(string queue, string body) => Call("queue", "push", queue, body);

        /// <summary>Takes the oldest pending message out of the queue.</summary>
        public string Pop(string queue) => Call("queue", "pop", queue);

        /// <summary>Looks at the oldest pending message without taking it.</summary>
        public string Peek(string queue) => Call("queue", "peek", queue);

        /// <summary>Confirms a message so its parked payload is dropped.</summary>
        public string Ack(string queue, string id) => Call("queue", "ack", queue, id);

        /// <summary>Requeues an in-flight message at its original position.</summary>
        public string Nack(string queue, string id) => Call("queue", "nack", queue, id);

        /// <summary>Empties the queue.</summary>
        public string Clear(string queue) => Call("queue", "clear", queue);

        /// <summary>Counts pending messages.</summary>
        public string Len(string queue) => Call("queue", "len", queue);
    }
}
