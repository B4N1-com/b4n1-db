# b4n1-db

**B4N1 पारिस्थितिकी तंत्र के लिए संप्रभु एम्बेडेड डेटा परत।** एक ही हैंडल
(`SovereignDB`) के माध्यम से आठ स्थानीय स्टोरेज इंजन — कोई क्लाइंट/सर्वर डेटाबेस
नहीं, कोई नेटवर्क इंजन नहीं, कोई डेमॉन नहीं, कोई बाहरी निर्भरता नहीं।

> **स्थिति: लाइब्रेरी + स्टैंडअलोन बाइनरी।** एक नेटिव लाइब्रेरी और एक
> स्टैंडअलोन `b4n1-db` CLI (KV, SQL, ग्राफ़, दस्तावेज़, क्यू, वेक्टर, ऑडिट,
> माइग्रेशन, ट्रांसफ़र, साथ ही ऑफ़लाइन `demo` और `docs`) प्रदान करता है। कोई MCP
> सर्वर नहीं। प्रकाशित SDK: PyPI, npm, NuGet, RubyGems, Packagist और Go modules
> (`sdks/` देखें)।

**भाषाएँ:** [العربية](README.ar.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Português (BR)](README.pt-BR.md) · [Русский](README.ru.md) · [中文](README.zh-CN.md)

---

## विषय-सूची

- [यह क्या है](#यह-क्या-है)
- [इंस्टॉलेशन](#इंस्टॉलेशन)
- [त्वरित शुरुआत](#त्वरित-शुरुआत)
- [उपप्रणालियाँ](#उपप्रणालियाँ)
- [SDK](#sdk)
- [लाइसेंस](#लाइसेंस)

---

## यह क्या है

`b4n1-db` एक एम्बेडेड मल्टी-इंजन डेटा परत है। आप एक डायरेक्टरी खोलते हैं और एक
ही हैंडल पाते हैं जो आठ स्टोरेज इंजन प्रदान करता है, सभी स्थानीय, सभी
नियतात्मक, सभी ऑडिट-योग्य।

| इंजन | कार्य |
|------|-------|
| KV | TTL के साथ कुंजी/मान, परमाणु काउंटर, CAS |
| SQL | कॉलम-आधारित स्टोरेज पर विश्लेषणात्मक SQL |
| Graph | हाइब्रिड खोज के साथ ज्ञान ग्राफ़ |
| Doc | फ़ील्ड क्वेरी के साथ दस्तावेज़ संग्रह |
| Queue | ack/nack के साथ FIFO क्यू |
| Vector | कोसाइन समानता वेक्टर इंडेक्स |
| Audit | टाइम ट्रैवल के साथ Merkle-DAG ऑडिट लॉग |
| Migration | स्कीमा माइग्रेशन रजिस्ट्री |

कोई डेमॉन नहीं। कोई नेटवर्क नहीं। कोई बाहरी डेटाबेस नहीं।

## इंस्टॉलेशन

### Python
```bash
pip install b4n1-db
```

### Node.js
```bash
npm install b4n1-db
```

### .NET
```bash
dotnet add package B4n1.Db
```

### Ruby
```bash
gem install b4n1-db
```

### PHP (Composer)
```bash
composer require b4n1/b4n1-db
```

### Go
```bash
go get github.com/B4N1-com/b4n1-db/sdks/go
```

## त्वरित शुरुआत

```bash
b4n1-db kv put mydb greeting "hello"
b4n1-db kv get mydb greeting
b4n1-db sql query mydb "SELECT * FROM users WHERE age > 25"
b4n1-db demo
b4n1-db docs
b4n1-db --licenses
```

## उपप्रणालियाँ

| कमांड | विवरण |
|-------|-------|
| `kv` | कुंजी/मान स्टोर: put, get, delete, scan, TTL |
| `sql` | विश्लेषणात्मक SQL इंजन |
| `graph` | ज्ञान ग्राफ़ और हाइब्रिड खोज |
| `doc` | दस्तावेज़ संग्रह |
| `queue` | ack/nack के साथ FIFO क्यू |
| `vector` | कोसाइन समानता वेक्टर इंडेक्स |
| `audit` | Merkle-DAG ऑडिट लॉग |
| `migration` | स्कीमा माइग्रेशन रजिस्ट्री |
| `transfer` | बैकअप, रीस्टोर और JSON ट्रांसफ़र |
| `demo` | एक-क्लिक ऑफ़लाइन डेमो |
| `docs` | अंतर्निहित दस्तावेज़ीकरण |

## SDK

`sdks/` डायरेक्टरी में 7 भाषाओं के लिए पतले रैपर हैं। प्रत्येक SDK नेटिव बाइनरी
एम्बेड करता है और इसे इस क्रम में हल करता है: `B4N1_DB_BIN` → एम्बेडेड `bin/` → `PATH`।

| SDK | डायरेक्टरी | पैकेज |
|-----|-----------|-------|
| Python | `sdks/python/` | PyPI: `b4n1-db` |
| Node.js | `sdks/node/` | npm: `b4n1-db` |
| .NET | `sdks/cs/` | NuGet: `B4n1.Db` |
| Ruby | `sdks/ruby/` | RubyGems: `b4n1-db` |
| PHP | `sdks/php/` | Packagist: `b4n1/b4n1-db` |
| Go | `sdks/go/` | Go modules |
| Java | `sdks/java/` | (Maven लंबित) |

## लाइसेंस

Business Source License 1.1 (BUSL-1.1)। देखें [LICENSE](../LICENSE) और [EULA.txt](../EULA.txt)।

---

*[B4N1 पारिस्थितिकी तंत्र](https://b4n1.com) का हिस्सा।*
