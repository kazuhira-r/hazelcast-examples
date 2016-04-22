const HazelcastClient = require("hazelcast-client").Client;
const Q = require("q");
const should = require("chai").should();

describe("Hazelcast JavaScript Client", () => {
    it("Getting Started", () => {
        return HazelcastClient.newHazelcastClient().then(client => {
            console.log("connectd.");

            const map = client.getMap("defaultMap");

                // put
            return map.put("key1", "value1")

                // get and verify
                .then(() => map.get("key1"))
                .then(value => value.should.equal("value1"))

                // remove
                .then(() => map.remove("key1"))
                .then(() => map.get("key1"))
                .then(value => should.equal(value, null))

                // empty?
                .then(() => map.isEmpty())
                .then(result => result.should.true)

                // put objects
                .then(() => {
                    const entries = [
                        { key: "key100", value: { name: "磯野カツオ", age: 11 } },
                        { key: "key200", value: { name: "磯野ワカメ", age: 9 } },
                        { key: "key300", value: { name: "フグ田タラオ", age: 3 } }
                    ];

                    return Q.all(entries.map(e => map.put(e.key, e.value)));
                })

                // size
                .then(() => map.size())
                .then(size => size.should.equal(3))

                // object get
                .then(() => map.get("key200"))
                .then(person => person.should.deep.equal({ name: "磯野ワカメ", age: 9 }))

                // clear
                .then(() => map.clear())

                // disconnect
                .fin(() => {
                    console.log("disconnect.");
                    client.shutdown()
                });
        });
    });

    it("specified, target hosts", () => {
        const Config = require("hazelcast-client").Config;
        const config = new Config.ClientConfig();
        config.networkConfig.addresses = [{ host: "localhost", port: 5702 }];

        return HazelcastClient.newHazelcastClient(config).then(client => {
            console.log("connectd.");

            const map = client.getMap("defaultMap");

                // put
            return map.put("key1", "value1")

                // get and verify
                .then(() => map.get("key1"))
                .then(value => value.should.equal("value1"))

                // clear
                .then(() => map.clear())

                // disconnect
                .fin(() => {
                    console.log("disconnect.");
                    client.shutdown()
                });
        });
    });
});
