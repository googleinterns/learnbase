"""Normalize vector.

Args: 
  vec: A list of 100 numbers

Returns:
  A list of normalized numbers
"""
def normalize(vec):
  magnitude = __dot(vec, vec)**0.5
  return [i/magnitude for i in vec]

"""Get cosine distance between two vectors

Args:
  vec1: A list of 100 numbers
  vec2: A list of 100 numbers

Returns:
  The dot product between the
  two vectors as a float
"""
def cosine_distance(vec1, vec2):
  return __dot(vec1, vec2)

# Gets dot product (Vectors are all of size 100)
def __dot(vec1, vec2):
  return sum([i*j for i, j in zip(vec1, vec2)])