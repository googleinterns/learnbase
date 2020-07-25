from model import get_words2vecs
from utils import *

# Get word2vec model
vocab, vectors, words2vecs = get_words2vecs("py/model/word_embedding.bin")
print(cosine_distance(words2vecs['science'], words2vecs['nurse']))
