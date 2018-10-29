package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encode Fields with Java MessageDigest
 */
public class MessageDigestFilter extends AbstractFilter {
    private String algorithm;
    private String salt;
    private List<String> fields;

    private MessageDigest md;

    @Override
    public void init() {
        algorithm = getProperty("algorithm", "SHA-512");
        salt = getProperty("salt", null);
        fields = getPropertyAsList("fields", null);
        verify(fields, "For the MessageDigestFilter fields must be defined!");

        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(salt.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }

        super.init();
    }

    @Override
    public void document(Document doc) {
        encode(doc);

        super.document(doc);
    }

    public void encode(Document doc) {
        for (String field : fields) {
            List<String> encodedValues = new ArrayList<>();
            List<String> fieldValues = doc.getFieldValues(field);
            if(fieldValues != null) {
                for (String value : doc.getFieldValues(field)) {
                    if (value != null) {
                        String encodedValue = encode(value);
                        encodedValues.add(encodedValue);
                    }
                }
                doc.setField(field, encodedValues);
            }
        }
    }

    public String encode(String value) {
        byte[] bytes = md.digest(value.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(bytes);
    }
}
