document = documentBuilder.document().create();
document.addField("foo", "bar");
document.addField("tbl", "bar");
document.addField("tbl", "alice");
document.addField("tbl", "bob");
document.addField("testen", input.getField("field500").getValue());

output.add(document);


output.add(input);