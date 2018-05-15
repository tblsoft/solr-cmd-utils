document = documentBuilder.document().create();
document.addField("foo", "bar");
document.addField("tbl", "bar");
document.addField("tbl", "alice");
document.addField("tbl", "bob");
document.setField("alice", input.getField("alice").getValues());
output.add(document);
output.add(input);