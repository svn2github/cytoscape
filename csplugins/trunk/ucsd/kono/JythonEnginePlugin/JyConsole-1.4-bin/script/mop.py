class Mop:
    def __init__(self, s):
        self.t_string = s

    def __str__(self):
        return self.t_string

class Mip:
    def __init__(self, s="le rugby"):
        self.t_string = s
        self.t_float = 1.0
        self.t_int = 1
        self.t_mop = Mop("c'est fort")
        self.t_unicode = u"grrrrrrrr"
        self.t_list = ["a", 1, 1.0, Mop("a poil")]
        self.t_tuple = (1, 2, 3, 4, "saas", u"fdf")
        self.t_dict = {
            "a": 1,
            "b": 1.0,
            "c": Mop("TIGER !!!"),
            "d": "salut"
            }

    def __str__(self):
        return str(self.t_float) + str (self.t_int) + " " + self.t_string \
               + str(self.t_mop)
