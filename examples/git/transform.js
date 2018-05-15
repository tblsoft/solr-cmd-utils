document = documentBuilder.document().create();
document.setField("commit", input.commit);
document.setField("author", input.author);
document.setField("date", input.date);
document.setField("message", input.message);
output.add(document);