import requests
from bs4 import BeautifulSoup
import json

data_dict = {}
array_of_dict_data = []


def getAll(page):
    html_doc = requests.get(page).text
    soup = BeautifulSoup(html_doc, 'lxml')
    tr = soup.find('div', {'id': 'download_content'}).find('table').find_all('tr')

    for i in range(1, len(tr)):
        data_dict = {}
        price_full = str(tr[i].findChildren("td", recursive=False)[0])
        if "PriceFull" in price_full:
            store = str(tr[i].findChildren("td", recursive=False)[2]).replace("<td>", "").replace("</td>", "")

            if store != "":
                company = str(tr[i].findChildren("td", recursive=False)[1]).replace("<td>", "").replace("</td>", "")
                download_link = "http://matrixcatalog.co.il/" + str(tr[i].findChildren("td", recursive=False)[7])
                download_link = download_link.replace("<a href=\"", "").replace("\"><u>לחץ כאן להורדה</u></a>", "").replace(
                    "<td>", "").replace("</td>", "").replace("\n", "").replace("\\", "/")
                index = download_link.find("-")
                id = download_link[(index+1)]+download_link[(index+2)]+download_link[(index+3)]
                data_dict['id'] = str(int(id))
                data_dict['name'] = store
                data_dict['link'] = download_link
                if company == "ויקטורי":
                    data_dict['brand']=str(2)
                elif company == "מחסני השוק":
                    data_dict['brand']=str(3)
                elif company == "סופר ברקת":
                    data_dict['brand'] = str(4)
                elif company == "ח. כהן":
                    data_dict['brand']=str(5)
                array_of_dict_data.append(data_dict)




def starter(link):
    getAll(link)
    jsonOBJ = json.dumps(array_of_dict_data, ensure_ascii=False)
    return jsonOBJ

