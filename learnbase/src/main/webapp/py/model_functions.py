import struct
from utils import *

def get_words2vecs(url):
  """
  Gets the word2vec data from the word_embedding.bin model.

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

      vocab[i] = word.decode("utf-8")
      vector = struct.unpack("<100f", file.read(binary_len))
      file.read(1)
      
      vectors[i] = normalize(vector)
      words2vecs[vocab[i]] = vectors[i]
      
  return vocab, vectors, words2vecs

def closest(words2vecs, vocab, word, n=10):
  """
  Gets `n` words that are closest to `word`

  Args:
    words2vecs: A dictionary mapping words to their corresponding word vectors.
    vocab: A list of words in vocabulary.
    word: A string of the word we're querying for.
    n: (optional) Number of words we return. Default value is 10.

  Returns:
    List of n words that are closest to the word we're querying for.
  """
  distances = []
  for other in vocab:
    if word != other:
      distances.append(distance(words2vecs, word, other))
  
  distances = _merge_sort(distances)
  distances.reverse()

  return distances[0:n]

def _merge_sort(values): 
  if len(values)>1: 
    m = len(values)//2
    left = values[:m] 
    right = values[m:] 
    left = _merge_sort(left) 
    right = _merge_sort(right) 

    values = [] 

    while len(left) > 0 and len(right) > 0: 
      if left[0][2] < right[0][2]: 
        values.append(left[0]) 
        left.pop(0) 
      else: 
        values.append(right[0]) 
        right.pop(0) 

    for i in left: 
      values.append(i) 
    for i in right: 
      values.append(i) 
                
  return values 