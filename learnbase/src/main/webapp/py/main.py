import sys
sys.path[0] = "py"

from model_functions import *

# Get word2vec model
vocab, vectors, words2vecs = get_words2vecs("py/model/word_embedding.bin")
closest_words = closest(words2vecs, vocab, vectors, "quantum_physics") 
print(closest_words)

