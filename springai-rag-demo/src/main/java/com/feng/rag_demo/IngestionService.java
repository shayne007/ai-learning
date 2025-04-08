package com.feng.rag_demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ingestion phase of the RAG demo
 *
 * @since 2025/4/1
 */
@Component
public class IngestionService implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final VectorStore vstore;

    @Value("classpath:/docs/data.pdf")
    private Resource dataPdf;

    public IngestionService(VectorStore vstore) {
        this.vstore = vstore;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("IngestionService is running");
        var reader = new PagePdfDocumentReader(dataPdf);
        TokenTextSplitter splitter = new TokenTextSplitter();
        vstore.accept(splitter.apply(reader.get()));

    }
}
