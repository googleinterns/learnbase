import word2vec

model = word2vec.load("model/word_embedding.bin")
print(model.vocab)