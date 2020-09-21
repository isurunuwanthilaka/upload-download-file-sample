CREATE TABLE merchant_documents (
 document_id serial PRIMARY KEY,
 user_id INTEGER ,
 file_name VARCHAR ,
 document_type VARCHAR ,
 document_format VARCHAR ,
 upload_dir VARCHAR
);