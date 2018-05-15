document = documentBuilder.document().create();
document.setField("foo", "bar");
document.setField("price", input.price);
output.add(document);