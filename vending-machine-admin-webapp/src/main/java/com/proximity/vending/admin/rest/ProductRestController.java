package com.proximity.vending.admin.rest;

import com.proximity.vending.admin.dto.ProductDTO;
import com.proximity.vending.admin.mapper.ProductDTOMapper;
import com.proximity.vending.admin.service.ProductService;
import com.proximity.vending.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    private final ProductDTOMapper productDTOMapper;

    @GetMapping
    public List<ProductDTO> findAll() {
        return this.productService.findAll()
                .stream()
                .map(this.productDTOMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/{code}")
    public ProductDTO findByCode(@PathVariable("code") String code) {
        Product product = this.productService.findByProductID(code);
        return this.productDTOMapper.map(product);
    }

    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO request) {
        Product savedProduct = this.productService.createProduct(request);
        return this.productDTOMapper.map(savedProduct);
    }

    @PatchMapping("/{code}/price")
    public ProductDTO changePrice(@PathVariable("code") String code, @RequestParam("price") BigDecimal price) {
        Product updatedProduct = this.productService.changePrice(code, price);
        return this.productDTOMapper.map(updatedProduct);
    }
}
