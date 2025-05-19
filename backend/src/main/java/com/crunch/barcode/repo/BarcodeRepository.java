package com.crunch.barcode.repo;

import com.crunch.barcode.entity.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

    Barcode findByBarcodeNumberAndEnabled(String barcodeNumber, boolean enabled);
    boolean existsByBarcodeNumberAndEnabled(String barcodeNumber, boolean enabled);
    List<Barcode> findByBarcodeNumberInAndEnabled(List<String> barcodeNumber, boolean enabled);
}