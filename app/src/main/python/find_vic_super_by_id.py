import requests
from bs4 import BeautifulSoup


def getLinkByID(page, given_id):
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
                download_link = download_link.replace("<a href=\"", "").replace("\"><u>לחץ כאן להורדה</u></a>",
                                                                                "").replace(
                    "<td>", "").replace("</td>", "").replace("\n", "").replace("\\", "/")
                index = download_link.find("-")
                id = download_link[(index + 1)] + download_link[(index + 2)] + download_link[(index + 3)]
                if int(id) != given_id:
                    continue
                else:
                    return download_link