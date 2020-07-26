import os
import sys
sys.path[0] = "py"
topic = sys.argv[1].replace(" ", "_")

import json
from model_functions import *

word_cache = None
word_exists = False

if os.path.exists("py/json/word_cache.json"):
  with open("py/json/word_cache.json", "r") as json_file:
    word_cache = json.load(json_file)
    if topic in word_cache:
      word_exists = True

if not word_exists:
  vocab, vectors, words2vecs = get_words2vecs("py/model/word_embedding.bin")
  distances = closest(words2vecs, vocab, vectors, topic) 

  closest_words = []
  for distance in distances:
    closest_words.append(distance[1])

  if word_cache is None:
    with open("py/json/word_cache.json", "w") as outfile:
      json.dump({topic: closest_words}, outfile)
  else:
    word_cache[topic] = closest_words
    with open("py/json/word_cache.json", "w") as outfile:
      json.dump(word_cache, outfile)
