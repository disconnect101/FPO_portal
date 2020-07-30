


def statuscode(code, dict=None):
    if not dict:
        response = {
            'statuscode': code
        }
    else:
        dict['statuscode'] = code
        response = dict

    return response