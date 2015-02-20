The SBox-SDN interface allows to set and retrieve SDN configuration parameters.

Currently (in v. 2.0), the interface is designed for DMT mechanism and allows to
set:
- R and C vectors,
- C vectors,
- configuration data.
In release 1.2 exactly two inter-domain tunnels per DC-DC connection are supported,
so R and C vectors have to have exactly two values (one per tunnel). 

In order to use the interface, the client should:
1. Import the eu.smartenit.sdn:interfaces-sboxsdn artifact
        <dependency>
            <groupId>eu.smartenit.sdn</groupId>
            <artifactId>interfaces-sboxsdn</artifactId>
            <version>${project.version}</version>
        </dependency>
2. Serialize the data being sent using Serialization.serialize function:
   - to send R and C vectors, serialize RCVectors object with referenceVector
     and compensationVector fields set,
   - to send C vector, serialize RCVectors object with compensationVector fields
     set,
   - to send configuration data, serialize ConfigData object.
3. Send a POST request to the REST service address and include serialized data
   in the body:
   - to send R and C vectors or C vector use address:
     "http://{ip}:8080" + URLs.BASE_PATH + URLs.DTM_R_C_VECTORS_PATH
   - to send configuration data use address:
     "http://{ip}:8080" + URLs.BASE_PATH + URLs.DTM_CONFIG_DATA_PATH

After successful configuration update, the server returns the latest values of
parameters. To retrieve the latest values of parameters without updating them,
the client should send a GET request to the same address as above.

The controller does not allow updating R & C vectors when the configuration is
not set.

Example conversation:
--- Request:
POST /smartenit/dtm/config-data HTTP/1.1
Host: localhost:8080
User-Agent: Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36
Origin: chrome-extension://hgmloofddffdnphfgcellkdfbfbjeloo
Content-Type: application/json 
Accept: */*
Accept-Encoding: gzip,deflate
Accept-Language: pl,en-US;q=0.8,en;q=0.6

{ 
	"entries" : [ 
	{ 
		"daRouterOfDPID" : "00:00:00:15:5d:72:a2:03",
		"remoteDcPrefix" : { 
			"prefix" : "10.10.1.0",
			"prefixLength" : 24
		},
		"tunnels" : [ 
		{ 
			"daRouterOfPortNumber" : 1,
			"tunnelID" : { 
				"localTunnelEndAddress" : { 
					"prefix" : "20.1.1.1",
					"prefixLength" : 32
				},
				"remoteTunnelEndAddress" : { 
					"prefix" : "10.1.1.1",
					"prefixLength" : 32
				},
				"tunnelName" : "TUNNEL11"
			}
        },
        { 
			"daRouterOfPortNumber" : 2,
            "tunnelID" : { 
				"localTunnelEndAddress" : { 
					"prefix" : "20.1.1.2",
                    "prefixLength" : 32
                },
                "remoteTunnelEndAddress" : { 
					"prefix" : "10.1.2.1",
                    "prefixLength" : 32
                },
                "tunnelName" : "TUNNEL12"
            }
        }
		]
    },
	{ 
		"daRouterOfDPID" : "00:00:00:15:5d:72:a2:03",
        "remoteDcPrefix" : { 
			"prefix" : "10.10.2.0",
            "prefixLength" : 24
        },
        "tunnels" : [ 
		{ 
			"daRouterOfPortNumber" : 3,
            "tunnelID" : { 
				"localTunnelEndAddress" : { 
					"prefix" : "20.1.1.1",
                    "prefixLength" : 32
                },
                "remoteTunnelEndAddress" : { 
					"prefix" : "10.1.1.1",
                    "prefixLength" : 32
                },
                "tunnelName" : "TUNNEL11"
            }
        },
        { 
			"daRouterOfPortNumber" : 4,
            "tunnelID" : { 
				"localTunnelEndAddress" : { 
					"prefix" : "20.1.1.2",
                    "prefixLength" : 32
                },
                "remoteTunnelEndAddress" : { 
					"prefix" : "10.1.2.1",
                    "prefixLength" : 32
                },
                "tunnelName" : "TUNNEL12"
            }
        }
        ]
    }
    ] 
}


--- Response headers 
Content-Type: application/json; charset=UTF-8 
Connection: keep-alive
Content-Length: 1015 
Server: Restlet-Framework/2.1rc1 
Accept-Ranges: bytes 
Date: Fri, 14 Nov 2014 11:04:46 GMT 
Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept 

{"entries":[{"remoteDcPrefix":{"prefix":"10.10.1.0","prefixLength":24},"daRouterOfDPID":"00:00:00:15:5d:72:a2:03","tunnels":[{"tunnelID":{"tunnelName":"TUNNEL11","localTunnelEndAddress":{"prefix":"20.1.1.1","prefixLength":32},"remoteTunnelEndAddress":{"prefix":"10.1.1.1","prefixLength":32}},"daRouterOfPortNumber":1},{"tunnelID":{"tunnelName":"TUNNEL12","localTunnelEndAddress":{"prefix":"20.1.1.2","prefixLength":32},"remoteTunnelEndAddress":{"prefix":"10.1.2.1","prefixLength":32}},"daRouterOfPortNumber":2}]},{"remoteDcPrefix":{"prefix":"10.10.2.0","prefixLength":24},"daRouterOfDPID":"00:00:00:15:5d:72:a2:03","tunnels":[{"tunnelID":{"tunnelName":"TUNNEL11","localTunnelEndAddress":{"prefix":"20.1.1.1","prefixLength":32},"remoteTunnelEndAddress":{"prefix":"10.1.1.1","prefixLength":32}},"daRouterOfPortNumber":3},{"tunnelID":{"tunnelName":"TUNNEL12","localTunnelEndAddress":{"prefix":"20.1.1.2","prefixLength":32},"remoteTunnelEndAddress":{"prefix":"10.1.2.1","prefixLength":32}},"daRouterOfPortNumber":4}]}]}