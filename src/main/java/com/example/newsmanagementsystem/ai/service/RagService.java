package com.example.newsmanagementsystem.ai.service;

import com.example.newsmanagementsystem.ai.dto.RagResultdto;
import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;

public interface RagService {
    public RagResultdto ask(SearchRequestdto request);

}
