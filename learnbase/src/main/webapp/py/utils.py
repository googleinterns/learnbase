# Normalize vector
def normalize(vec):
  magnitude = __dot(vec, vec)**0.5
  return [i/magnitude for i in vec]

def cosine_distance(vec1, vec2):
  return __dot(vec1, vec2)

# Gets dot product (Vectors are all of size 100)
def __dot(vec1, vec2):
  return sum([i*j for i, j in zip(vec1, vec2)])