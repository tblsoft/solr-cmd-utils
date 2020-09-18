// ParagraphAttributeExtractor
// set params
var html =  "<html>\n" +
            "<body>\n" +
            "    <div class=\"product-info-wrapper\">\n" +
            "        <p><strong>Decksohle:</strong><br>Leder</p>\n" +
            "        <p><strong>Form:</strong><br>Sandale/Pantolette</p>\n" +
            "        <p><strong>Futter:</strong><br>Leder</p>\n" +
            "        <p><strong>Farbe:</strong><br>Grau</p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
var jsoupDoc = htmlParser.parseInput(html, "");
var selector = ".product-info-wrapper > p";
var doc = docs.get(0);

// parse
var elements = jsoupDoc.select(selector);
elements.forEach(function(element) {
    var splitted = element.text().split(":");
    var key = "attr_"+splitted[0].trim();
    var value = splitted[1].trim();
    doc.addField(key, value);
    print(key+": "+value);
});
