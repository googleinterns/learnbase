def normalize(vec):
  """
  Normalize vector.

  Args: 
    vec: A list of 100 numbers

  Returns:
    A normalized list.
  """
  magnitude = __dot(vec, vec)**0.5
  return [i/magnitude for i in vec]

def distance(word2vecDict, word1, word2):
  """
  Get distance between two vectors

  Args:
    word2vecDict: Dictionary of word2vecs
    word1: string, first word
    word2: string, second word
  
  Return:
    tuple of words, and distance between them
  """
 
  d = cosine_distance(word2vecDict[word1], word2vecDict[word2])
  return word1, word2, d

def cosine_distance(vec1, vec2):
  """
  Get cosine distance between two vectors

  Args:
    vec1: A list of 100 numbers
    vec2: A list of 100 numbers

  Returns:
    The dot product between the
    two vectors as a float.
  """
  return __dot(vec1, vec2)

# Gets dot product (Vectors are all of size 100)
def __dot(vec1, vec2):
  return sum([i*j for i, j in zip(vec1, vec2)])