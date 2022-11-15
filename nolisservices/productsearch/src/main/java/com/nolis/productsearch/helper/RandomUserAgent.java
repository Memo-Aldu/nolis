package com.nolis.productsearch.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

@Slf4j @Component
public class RandomUserAgent {
    private static final Random random = new Random();
    private static final List<String> osList = Arrays.asList(
            "Macintosh; Intel Mac OS X 10_15_7",
            "Macintosh; Intel Mac OS X 10_15_5",
            "Macintosh; Intel Mac OS X 10_11_6",
            "Macintosh; Intel Mac OS X 10_6_6",
            "Macintosh; Intel Mac OS X 10_9_5",
            "Macintosh; Intel Mac OS X 10_10_5",
            "Macintosh; Intel Mac OS X 10_7_5",
            "Macintosh; Intel Mac OS X 10_11_3",
            "Macintosh; Intel Mac OS X 10_10_3",
            "Macintosh; Intel Mac OS X 10_6_8",
            "Macintosh; Intel Mac OS X 10_10_2",
            "Macintosh; Intel Mac OS X 10_10_3",
            "Macintosh; Intel Mac OS X 10_11_5",
            "Windows NT 10.0; Win64; x64",
            "Windows NT 10.0; WOW64",
            "Windows NT 10.0"
    );

    public String getRandomUserAgent() {
        return "Mozilla/5.0 (" + osList.get(random.nextInt(osList.size())) + ") AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/ " + Math.floor(Math.random() * 4) + 100  + ".0. + " +
                Math.floor(Math.random() * 190) + 4100 + "." + Math.floor(Math.random() * 50) + 140 + "Safari/537.36";
    }
}
