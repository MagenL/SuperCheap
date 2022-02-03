import requests
from bs4 import BeautifulSoup
import json
import io
import gzip
import json
import urllib
import xmltodict



dict_data = {}
array_of_dict_data = []

replace_start = """<td><a href=\""""
replace_end = """" target="_blank">לחץ להורדה</a></td>"""
replace_condition_amp = "amp;"
replace_http = "http"


def get_id_through_super_name(name):
    times = 0
    number = 0
    for let in name:
        if str(let).isdigit():
            if times > 0:
                number = (number * 10) + int(let)

                # number *= 10 + int(word)
            elif times == 0:
                number = int(let)
                times += 1
    return number


def getAll(array_of_dict_data, page):
    html_doc = requests.get(page).text
    soup = BeautifulSoup(html_doc, 'lxml')

    tr = soup.find_all('tr', {'class': 'webgrid-alternating-row'}) + soup.findAll('tr', {'class': 'webgrid-row-style'})

    for i in range(0, len(tr)):
        dict_data={}
        if str(tr[i].findChildren("td", recursive=False)[5]) != "<td></td>":
            downloadLink = str(tr[i].findChildren("td", recursive=False)[0])
            dict_data["link"] = downloadLink.replace(replace_start, "").replace(replace_end, "").replace(
                replace_condition_amp, "").replace(replace_http, "https")
            dict_data["name"] = str(tr[i].findChildren("td", recursive=False)[5]).replace("<td>", "").replace("</td>",
                                                                                                              "")
            dict_data["id"] = get_id_through_super_name(dict_data["name"])
            array_of_dict_data.append(dict_data)


def starter(url):
    for i in range(1, 23):
        getAll(array_of_dict_data, str(url) + str(i))
    return array_of_dict_data
    # jsonOBJ = json.dumps(array_of_dict_data, ensure_ascii=False)
    # return jsonOBJ
