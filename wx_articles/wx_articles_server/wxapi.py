from flask import Flask,request
import json
from mongo_helper import MongoHelper
 
app=Flask(__name__)

helper = MongoHelper()


@app.route("/wxapi",methods=["POST"])
def wxapi():
    article = process_data(request.form.get('article'))
    helper.do_insert(article)
    return json.dumps({"result": "ok"}, ensure_ascii=False)
 
def process_data(s):
    kv_li = s.replace("{", "").replace("}", "").split(",")
    dic = {}
    for kv in kv_li:
        kv = kv.split("=", 1)
        dic[kv[0]] = kv[1]
    return dic


if __name__ == "__main__":
    app.run('0.0.0.0', 8080, debug=True)
