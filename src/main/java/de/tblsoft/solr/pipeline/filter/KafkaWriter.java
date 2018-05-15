package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Created by tblsoft on 24.03.18.
 */
public class KafkaWriter extends AbstractFilter {

    private Producer<String, String> producer;

    private String idField = "id";

    private String topic;

    @Override
    public void init() {

        try {
            String kafkaServers = getProperty("kafkaServers", "localhost:9092");
            String clientId = getProperty("clientId", "solr-cmd-utils");
            topic = getProperty("topic", null);


            producer = createProducer(kafkaServers, clientId);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        super.init();
    }


    private static Producer<String, String> createProducer(String kafkaServers, String clientId) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<String, String>(props);
    }

    @Override
    public void document(Document document) {
        try {
            String id = document.getFieldValue(idField);
            final ProducerRecord<String, String> record =
                    new ProducerRecord<String, String>(topic, id, document.toString());
            RecordMetadata metadata = producer.send(record).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.document(document);
    }



    @Override
    public void end() {
        producer.flush();
        producer.close();
        super.end();

    }

}
