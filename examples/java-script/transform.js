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

// parse
var elements = jsoupDoc.select(selector);
elements.forEach(function(element) {
    var splitted = element.text().split(":");
    var key = splitted[0];
    var value = splitted[1];
    print("attr_"+key+": "+value);
});
