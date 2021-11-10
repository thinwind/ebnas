function detail(method) {
    let res = null;
    switch (method) {
        case 'GET':
            res = [
                {
                    "success": true,
                    "data": [
                      {
                        "id": 100000,
                        "name": "demo1",
                        "nodes": [
                          {
                            "id": 100001,
                            "ip": "127.0.0.1",
                            "port": 8088,
                            "status": "UNKNOWN",
                            "createdAt": 1636334885711,
                            "modifiedAt": 1636526347340
                          },
                          {
                            "id": 100002,
                            "ip": "127.0.0.2",
                            "port": 8088,
                            "status": "UNKNOWN",
                            "createdAt": 1636334885711,
                            "modifiedAt": 1636526347340
                          }
                        ],
                        "createdAt": 1636334885698,
                        "modifiedAt": 1636334885698
                      },
                      {
                        "id": 100000,
                        "name": "demo2",
                        "nodes": [
                          {
                            "id": 100001,
                            "ip": "127.0.1.1",
                            "port": 8088,
                            "status": "UNKNOWN",
                            "createdAt": 1636334885711,
                            "modifiedAt": 1636526347340
                          }
                        ],
                        "createdAt": 1636334885698,
                        "modifiedAt": 1636334885698
                      }
                    ],
                    "traceId": "1636526219151@192.168.34.228",
                    "host": "192.168.34.228"
                  }
            ];
            break;
        default:
            res = null;
    }
    return res;
}

module.exports = detail;