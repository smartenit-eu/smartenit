The SBox-SDN interface allows to set and retrieve SDN configuration parameters.

Currently (in v. 1.0), the interface is designed for DMT mechanism and allows to
set:
- R and C vectors,
- C vector,
- configuration data.

In order to use the interface, the client shoud:
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

After successful configuration update, the server returns the current values of
parameters. To retrieve the current values of parameters without updating them,
the client should send a GET request to the same address as above.

The controller does not allow updating R & C vectors when the configuration is
not set.

Example conversation:
--- Request:
POST /smartenit/dtm/r-c-vectors HTTP/1.1
Host: localhost:8080
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: null
Accept-Encoding: gzip, deflate
DNT: 1
Content-Type: application/json; charset=UTF-8
Content-Length: 1002
Connection: keep-alive
Pragma: no-cache
Cache-Control: no-cache

{
  "referenceVector": {
    "vectorValues": [
      {
        "value": 1000000000,
        "linkID": {
          "class": "eu.smartenit.sbox.db.dto.SimpleLinkID",
          "localLinkID": "link1",
          "localIspName": "isp1"
        }
      },
      {
        "value": 2000000000,
        "linkID": {
          "class": "eu.smartenit.sbox.db.dto.SimpleLinkID",
          "localLinkID": "link2",
          "localIspName": "isp1"
        }
      }
    ],
    "sourceAsNumber": 1,
    "thetaCollection": null
  },
  "compensationVector": {
    "vectorValues": [
      {
        "value": 1000000,
        "linkID": {
          "class": "eu.smartenit.sbox.db.dto.SimpleLinkID",
          "localLinkID": "link1",
          "localIspName": "isp1"
        }
      },
      {
        "value": -1000000,
        "linkID": {
          "class": "eu.smartenit.sbox.db.dto.SimpleLinkID",
          "localLinkID": "link2",
          "localIspName": "isp1"
        }
      }
    ],
    "sourceAsNumber": 1
  }
}
--- Response
HTTP/1.1 200 OK
Content-Type: application/json; charset=UTF-8
Connection: keep-alive
Content-Length: 630
Server: Restlet-Framework/2.1rc1
Accept-Ranges: bytes
Date: Tue, 27 May 2014 08:52:58 GMT
Vary: Accept-Charset, Accept-Encoding, Accept-Language, Accept

{"referenceVector":{"vectorValues":[{"value":1000000000,"linkID":{"class":"eu.smartenit.sbox.db.dto.SimpleLinkID","localLinkID":"link1","localIspName":"isp1"}},{"value":2000000000,"linkID":{"class":"eu.smartenit.sbox.db.dto.SimpleLinkID","localLinkID":"link2","localIspName":"isp1"}}],"sourceAsNumber":1,"thetaCollection":null},"compensationVector":{"vectorValues":[{"value":1000000,"linkID":{"class":"eu.smartenit.sbox.db.dto.SimpleLinkID","localLinkID":"link1","localIspName":"isp1"}},{"value":-1000000,"linkID":{"class":"eu.smartenit.sbox.db.dto.SimpleLinkID","localLinkID":"link2","localIspName":"isp1"}}],"sourceAsNumber":1}}