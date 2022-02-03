import requests
from bs4 import BeautifulSoup

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


def getSpecific(page, super_to_find):
    html_doc = requests.get(page).text
    soup = BeautifulSoup(html_doc, 'lxml')

    tr = soup.find_all('tr', {'class': 'webgrid-alternating-row'}) + soup.findAll('tr', {'class': 'webgrid-row-style'})

    for i in range(0, len(tr)):
        dict_data = {}
        if str(tr[i].findChildren("td", recursive=False)[5]) != "<td></td>":
            downloadLink = str(tr[i].findChildren("td", recursive=False)[0])
            temp_link = str(downloadLink.replace(replace_start, "").replace(replace_end, "").replace(
                replace_condition_amp, "").replace(replace_http, "https"))
            temp_name = str(tr[i].findChildren("td", recursive=False)[5]).replace("<td>", "").replace("</td>",
                                                                                                      "")
            temp_id = get_id_through_super_name(temp_name)
            print("temp id = " + str(temp_id))

            # print("dict_data['link'] "+temp_link+"\n dict_data['name'] "+temp_name + "\n dict_data['id']"+temp_id)
            if str(temp_id) == str(super_to_find):
                return temp_link


def finder(super_id):
    for i in range(1, 24):
        link = getSpecific("http://prices.shufersal.co.il/FileObject/UpdateCategory?catID=2&page=" + str(i), super_id)
        if link is not None:
            return link