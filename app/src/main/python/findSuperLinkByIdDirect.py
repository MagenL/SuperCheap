import requests
from bs4 import BeautifulSoup

replace_start = """<td><a href=\""""
replace_end = """" target="_blank">לחץ להורדה</a></td>"""
replace_condition_amp = "amp;"
replace_http = "http"


def getSpecific(page):
    html_doc = requests.get(page).text
    soup = BeautifulSoup(html_doc, 'lxml')

    tr = soup.find_all('tr', {'class': 'webgrid-alternating-row'}) + soup.findAll('tr', {'class': 'webgrid-row-style'})

    for i in range(0, len(tr)):

        if str(tr[i].findChildren("td", recursive=False)[5]) != "<td></td>":
            downloadLink = str(tr[i].findChildren("td", recursive=False)[0])
            temp_link = str(
                downloadLink.replace(replace_start, "").replace(replace_end, "").replace(replace_condition_amp,
                                                                                         "").replace(replace_http,
                                                                                                     "https"))
            return temp_link


def finder(url,superId):
    return getSpecific(str(url) + str(superId))



