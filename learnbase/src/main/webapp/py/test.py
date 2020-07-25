import unittest
from utils import *

class TestUtils(unittest.TestCase):
  def test_normalize_1(self):
    self.assertEqual(normalize([3,4]), [3/5, 4/5])

  def test_normalize_2(self):
    self.assertEqual(normalize([1,2,2]), [1/3, 2/3, 2/3])

  def test_distance(self):
    sampleDict = {"a": [1], "b":[2], "c": [3]}
    self.assertEqual(distance(sampleDict, "a", "b"), [('a', 'b', 2)])

  def test_cosine_dist_1(self):
    self.assertEqual(cosine_distance([3,4], [2,1]), 10)

  def test_cosine_dist_2(self):
    self.assertEqual(cosine_distance([9,4,3], [2,1,6]), 40)

if __name__ == "__main__":
  unittest.main()