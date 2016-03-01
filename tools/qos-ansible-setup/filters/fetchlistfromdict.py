def fetchlistfromdict(d, l):
    result = []
    for item in l:
        result.append(d[item])

    return result


def string_filter(string_list, filter):
    return [filter % s for s in string_list]


def all_filter(list, val):
    return all(val == i for i in list)


def any_filter(list, val):
    return any(val == i for i in list)


class FilterModule(object):
    def filters(self):
        return {
            'fetchlistfromdict': fetchlistfromdict,
            'string_filter': string_filter,
            'all_filter': all_filter,
            'any_filter': any_filter
        }
