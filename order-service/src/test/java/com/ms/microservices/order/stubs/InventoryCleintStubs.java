package com.ms.microservices.order.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryCleintStubs {

    public static void stubInventoryCall(String productName, Integer quantity) {
        if (quantity <= 100) {
            stubFor(get(urlEqualTo("/api/inventory?ProductName=" + productName + "&Quantity=" + quantity))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                         "message": "Busca Realizada",
                                         "data": true
                                     }
                                    """)));
        }else{
            stubFor(get(urlEqualTo("/api/inventory?ProductName=" + productName + "&Quantity=" + quantity))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    {
                                         "message": "Busca Realizada",
                                         "data": false
                                     }
                                    """)));
        }
    }
}
