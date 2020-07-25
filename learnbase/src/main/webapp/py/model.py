import itertools
import struct
from utils import *

# Gets words2vec model created from Word2Vec python module
def get_words2vecs(url):
  words2vecs = {}
  with open(url, "rb") as file:
    header = file.readline()
    vocab_size, vector_size = list(map(int, header.split()))

    vocab = [None]*vocab_size
    vectors = [[None for _ in range(vector_size)] for _ in range(vocab_size)]
    binary_len = 400

    for i in range(vocab_size):
      word = b""
      while True:
        char = file.read(1)
        if char == b" ":
          break
        word += char

      vocab[i] = word.decode("utf-8").strip('\n')
      vector = struct.unpack("100f", file.read(400))
      
      vectors[i] = normalize(vector)
      words2vecs[vocab[i]] = vectors[i]
      
  return vocab, vectors, words2vecs
