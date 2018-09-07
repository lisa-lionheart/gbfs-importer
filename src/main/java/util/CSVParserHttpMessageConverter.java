package util;

import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lisa Croxford <lisac@softwarerad.com>
 */
@AllArgsConstructor
public class CSVParserHttpMessageConverter implements HttpMessageConverter<List<CSVRecord>> {

    final CSVFormat format;

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN);
    }

    @Override
    public List<CSVRecord> read(Class<? extends List<CSVRecord>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        return new CSVParser(new InputStreamReader(inputMessage.getBody(), Charset.defaultCharset()), format).getRecords();
    }

    @Override
    public void write(List<CSVRecord> csvRecords, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

    }
}
