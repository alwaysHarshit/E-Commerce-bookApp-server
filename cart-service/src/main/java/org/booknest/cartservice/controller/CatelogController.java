package org.booknest.cartservice.controller;

import org.booknest.cartservice.service.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CatelogController {

    @Autowired
    private CartServiceImpl cartService;

    @GetMapping("/getBook/{bookId}")
    public void  getBook(@PathVariable("bookId") int bookId){

    }


}
