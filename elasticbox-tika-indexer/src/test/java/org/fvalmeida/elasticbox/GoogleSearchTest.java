package org.fvalmeida.elasticbox;

import com.google.gson.Gson;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by fvalmeida on 8/21/15.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class GoogleSearchTest {

    @Test
    public void search() throws IOException {
        String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
        String search = "siteempresas.bovespa.com.br";
        String charset = "UTF-8";

        URL url = new URL(google + URLEncoder.encode(search, charset));
        Reader reader = new InputStreamReader(url.openStream(), charset);
        GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);

        // Show title and URL of all results.
        for (GoogleResults.Result result : results.getResponseData().getResults()) {
            System.out.println(ToStringBuilder.reflectionToString(result, ToStringStyle.MULTI_LINE_STYLE));
            System.out.println();
        }

    }

    @Data
    class GoogleResults {

        private ResponseData responseData;

        @Data
        class ResponseData {
            private List<Result> results;
        }

        @Data
        class Result {
            private String url;
            private String title;
            private String unescapedUrl;
            private String visibleUrl;
            private String cacheUrl;
            private String titleNoFormatting;
            private String content;
        }

    }
}