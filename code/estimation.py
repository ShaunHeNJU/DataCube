# -*- coding:utf8 -*-

"""
    author: Shaun He
    date  : 2018-09-12
    email : 709278724@qq.com
"""

import math
from scipy.stats import chi2


def estimation(n, N, d, f, attributes):
    """
        n (int)           : 采样的样本数
        N (int)           : 记录数
        d (int)           : 样本中出现的属性值的个数
        f (list)          : 是一个映射函数，属性值出现个数的统计
        attributes (list) : 每个属性值出现的个数

    """

    if chi_square_test(n, d, attributes):
        return sjack(n, N, d, f)
    else:
        return shloss(n, N, d, f)


def chi_square_test(n, d, attributes):
    """

        卡方检验,检测样本的分布状况 True:无偏，False:有偏

        n (int)           : 采样的样本数
        d (int)           : 样本中出现的属性值的个数
        attributes (list) : 每个属性值出现的个数

    """

    n_average = n / d
    u = 0
    for j in range(len(attributes)):
        u += math.pow((attributes[j] - n_average), 2) / n_average

    # 置信度需要确定，这个方面写的不是很清楚，要考虑上下界的问题
    x = chi2.ppf(0.975,d-1)
    if u <= x:
        return True
    else:
        return False


def shloss(n, N, d, f):
    """
        shloosser's estimator 用于有偏样本的估计

        n (int)           : 采样的样本数
        N (int)           : 记录数
        d (int)           : 样本中出现的属性值的个数
        f (list)          : 是一个映射函数，属性值出现个数的统计
    """

    q = n / N
    numerator = 0
    denominator = 0
    for i in range(n):
        numerator += pow(1-q, i+1) * f[i]
        denominator += pow(1-q, i) * (i+1) * f[i]
    return d + f[0] * numerator / denominator


def sjack(n, N, d, f):
    """
        a smoothed jackknife estimator 用于无偏样本的估计

        n (int)           : 采样的样本数
        N (int)           : 记录数
        d (int)           : 样本中出现的属性值的个数
        f (list)          : 是一个映射函数，属性值出现个数的统计
    """

    D0 = (d - (f[0] / n)) / (1 - (N - n + 1) * f[0] / (n * N))
    approx_N = N / D0
    return (d + N * h(N, approx_N, n) * g(approx_N, n-1, N) * square_r(n, N, D0, f)) / (1 - (N - approx_N - n + 1) * f[0] / (n*N))


def square_r(n, N, D, f):
    """

        n (int)           : 采样的样本数
        N (int)           : 记录数
        d (int)           : 样本中出现的属性值的个数
        f (list)          : 是一个映射函数，属性值出现个数的统计
    """
    temp = 0
    for i in range(n):
        temp += i * (i + 1) * f[i]
    return temp * (N - 1) * D / (N * n * (n - 1)) + D / N - 1


def h(N, N1, n):
    """
        n (int)           : 采样的样本数
        N1(int)           : 近似记录数
        N(int)            : 记录数

        h函数的优化，防止由于数过大造成gamma函数无法计算
        math.gamma(N - N1 + 1) * math.gamma(N - n + 1) / (math.gamma(N - n - N1 + 1) * math.gamma(N + 1))
    """

    result = 0
    for i in range(n):
        result += math.log(10,N - N1 - i) - math.log(10, N - i)
    return math.pow(10, result)


def g(x, n, N):
    """
        x (int)           : g函数的自变量
        n (int)           : 采样的样本数
        N (int)           : 记录数
    """

    result = 0
    for k in range(1, n + 1):
        result += 1 / (N - x - n + k)
    return result