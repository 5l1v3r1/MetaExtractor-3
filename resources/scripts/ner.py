#import nltk
#from nameparser.parser import HumanName
#
#def get_human_names(text):
#    tokens = nltk.tokenize.word_tokenize(text)
#    pos = nltk.pos_tag(tokens)
#    sentt = nltk.ne_chunk(pos, binary = False)
#    person_list = []
#    person = []
#    name = ""
#    for subtree in sentt.subtrees(filter=lambda t: t.label() == 'PERSON'):
#        for leaf in subtree.leaves():
#            person.append(leaf[0])
#        if len(person) > 1: #avoid grabbing lone surnames
#            for part in person:
#                name += part + ' '
#            if name[:-1] not in person_list:
#                person_list.append(name[:-1])
#            name = ''
#        person = []
#
#    return (person_list)
##nltk.download()
#f = open("out.txt", 'r')
#text = f.read()
#
#names = get_human_names(text)
#print("LAST, FIRST")
#for name in names: 
#	last_first = HumanName(name).last + ', ' + HumanName(name).first
#	print(last_first)
######################
#def extract_candidate_words(text, good_tags=set(['JJ','JJR','JJS','NN','NNP','NNS','NNPS'])):
#    import itertools, nltk, string
#
#    # exclude candidates that are stop words or entirely punctuation
#    punct = set(string.punctuation)
#    stop_words = set(nltk.corpus.stopwords.words('english'))
#    # tokenize and POS-tag words
#    tagged_words = itertools.chain.from_iterable(nltk.pos_tag_sents(nltk.word_tokenize(sent)
#                                                                    for sent in nltk.sent_tokenize(text)))
#    # filter on certain POS tags and lowercase all words
#    candidates = [word.lower() for word, tag in tagged_words
#                  if tag in good_tags and word.lower() not in stop_words
#                  and not all(char in punct for char in word)]
#
#    return candidates
#
#def score_keyphrases_by_textrank(text, n_keywords=0.05):
#    from itertools import takewhile, tee, zip_longest
#    import networkx, nltk
#	
#    # tokenize for all words, and extract *candidate* words
#    words = [word.lower()
#             for sent in nltk.sent_tokenize(text)
#             for word in nltk.word_tokenize(sent)]
#    candidates = extract_candidate_words(text)
#    # build graph, each node is a unique candidate
#    graph = networkx.Graph()
#    graph.add_nodes_from(set(candidates))
#    # iterate over word-pairs, add unweighted edges into graph
#    def pairwise(iterable):
#        """s -> (s0,s1), (s1,s2), (s2, s3), ..."""
#        a, b = tee(iterable)
#        next(b, None)
#        return zip_longest(a, b)
#    for w1, w2 in pairwise(candidates):
#        if w2:
#            graph.add_edge(*sorted([w1, w2]))
#    # score nodes using default pagerank algorithm, sort by score, keep top n_keywords
#    ranks = networkx.pagerank(graph)
#    if 0 < n_keywords < 1:
#        n_keywords = int(round(len(candidates) * n_keywords))
#    word_ranks = {word_rank[0]: word_rank[1]
#                  for word_rank in sorted(ranks.items(), key=lambda x: x[1], reverse=True)[:n_keywords]}
#    keywords = set(word_ranks.keys())
#    # merge keywords into keyphrases
#    keyphrases = {}
#    j = 0
#    for i, word in enumerate(words):
#        if i < j:
#            continue
#        if word in keywords:
#            kp_words = list(takewhile(lambda x: x in keywords, words[i:i+10]))
#            avg_pagerank = sum(word_ranks[w] for w in kp_words) / float(len(kp_words))
#            keyphrases[' '.join(kp_words)] = avg_pagerank
#            # counter as hackish way to ensure merged keyphrases are non-overlapping
#            j = i + len(kp_words)
#    
#    return sorted(keyphrases.items(), key=lambda x: x[1], reverse=True)
#	
#	
#f = open("out1.txt", 'r')
#text = f.read()	
#	
#key = score_keyphrases_by_textrank(text)
#
#for k in key:
#	print(k)
#################
import RAKE
import operator

rake_object = RAKE.Rake("resources/scripts/SmartStoplist.txt")

sample_file = open("resources/scripts/out.txt", 'r')
text = sample_file.read()
keywords = rake_object.run(text, 5, 3, 4)
print("Keywords:", keywords)
	
	

	