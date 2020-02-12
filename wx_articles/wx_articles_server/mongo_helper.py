from pymongo import MongoClient
from hashlib import md5


class MongoHelper(object):

    def __init__(self):
        self.client = MongoClient("mongodb://xfs:123456@106.14.158.63:27017/weixin")
        self.articles = self.client['weixin']['articles']

    def __del__(self):
        self.client.close()
    
    def get_article_hash(self, article):
        return md5(str(article).encode("utf-8")).hexdigest()

    def do_insert(self, article):
        article_hash = self.get_article_hash(article)
        count = self.articles.count_documents({'_id': article_hash})
        if count == 0:
            article['_id'] = article_hash
            self.articles.insert_one(article)
    

if __name__ == "__main__":
    helper = MongoHelper()
    article1 = {"name": "逆向小白", "url": "https://www.baidu.com"}
    article2 = {"name": "逆向小白", "url": "https://www.hahah.com"}
    article3 = {"name": "逆向小白", "url": "https://www.baidu.com"}
    helper.do_insert(article1)
    helper.do_insert(article2)
    helper.do_insert(article3) 
