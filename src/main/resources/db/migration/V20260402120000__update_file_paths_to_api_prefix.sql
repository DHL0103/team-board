UPDATE posts SET content = REPLACE(content, '/files/', '/api/files/') WHERE content LIKE '%/files/%';
