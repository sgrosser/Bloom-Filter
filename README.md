# Bloom-Filter
We first calculated that the optimal number of hash functions in this use case would be 7.

We generate 7 hash functions of the form (a + bx mod p) mod m and use them to preprocess the Bloom filter.

The filter itself is implemented as an array of integers. We use bit shifting to access each individual bit.

100 random words are created and tested for membership in the Bloom filter. If it was not added and the membership test is positive, we increase the count in the false positive rate.

We run the eperiment several times to get an average false positive rate.
