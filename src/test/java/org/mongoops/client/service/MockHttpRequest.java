package org.mongoops.client.service;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.MessageFormat;

class MockHttpRequest
    implements RequestMatcher {

    private HttpMethod method;
    private Resource resource;

    MockHttpRequest(HttpMethod method, String type, String name, Object... args)
        throws IOException {

        this.method = method;
        this.resource = mockJsonPayload(type, name, args);
    }

    @Override
    public void match(ClientHttpRequest request)
        throws IOException, AssertionError {

        if (!request.getMethod().equals(this.method)) {
            throw new AssertionError(String.format("Request method does not match expected method '%s'.", method.toString()));
        }

        String requestBody = ((MockClientHttpRequest) request).getBodyAsString();
        String expectedBody = StreamUtils.copyToString(this.resource.getInputStream(), Charset.forName("UTF-8"));

        try {
            JSONAssert.assertEquals(expectedBody, requestBody, false);
        } catch (JSONException e) {
            throw new AssertionError("Requested JSON does not match expected request.", e);
        }
    }

    static Resource mockJsonPayload(String type, String name, Object... args)
        throws IOException {

        Reader r = new InputStreamReader(
            new ClassPathResource("mongops/fixtures/" + type + "/" + name + ".json").getInputStream() );
        StringWriter s = new StringWriter();

        char[] buffer = new char[128];
        int len;

        while ((len = r.read(buffer)) != -1) {
            s.write(buffer, 0, len);
        }

        return new InputStreamResource(
            new ByteArrayInputStream(MessageFormat.format(s.toString(), args).getBytes()) );
    }
}
