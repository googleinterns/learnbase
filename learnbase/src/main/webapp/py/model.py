import itertools
import struct
from utils import *

def get_words2vecs(url):
  """Gets the word2vec data from the word_embedding.bin model.

  Converts this data from bytestrings to words corresponding
  to their word vectors.

  Args:
    url: The path to the data that's downloaded.

  Returns:
    vocab: A list of all of the words in the vocabulary.
    vectors: A list of all of the word embeddings.
    words2vecs: A dictionary that maps each word to its corresponding embedding.
  """
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
