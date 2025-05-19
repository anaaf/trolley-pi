package com.crunch.barcode.controller;

import com.crunch.barcode.service.BarcodeService;
import com.crunch.user.api.BarcodeApi;
import com.crunch.user.model.BarcodeRequest;
import com.crunch.user.model.BarcodeResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class BarcodeController implements BarcodeApi {
    private final BarcodeService barcodeService;

    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @Override
    public ResponseEntity<BarcodeResponse> addBarcode(BarcodeRequest barcodeRequest) {
        BarcodeResponse response = barcodeService.addBarcode(barcodeRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BarcodeResponse> getBarcode(String barcodeNumber) {
        BarcodeResponse response = barcodeService.getBarcodeByBarcodeNumber(barcodeNumber);
        return ResponseEntity.ok(response);
    }
}
