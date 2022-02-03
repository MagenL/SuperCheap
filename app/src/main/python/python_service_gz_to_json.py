import io
import gzip
import json
import urllib
import xmltodict

def download(url):
    # Download SEED database

    print(('Downloading SEED Database from: {}'.format(url)))
    response = urllib.request.urlopen(url)
    compressed_file = io.BytesIO(response.read())
    decompressed_file = gzip.GzipFile(fileobj=compressed_file)
    return ((convert_to_json(decompressed_file.read())))


def convert_to_json(someFile):
    data_dict = xmltodict.parse(someFile.decode('utf-8'))
    json_data = json.dumps(data_dict, ensure_ascii=False)
    return json_data