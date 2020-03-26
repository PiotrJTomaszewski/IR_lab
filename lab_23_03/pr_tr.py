import numpy as np

#L1  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L1  = [0, 1, 1, 0, 1, 0, 0, 0, 0, 0]
L2  = [1, 0, 0, 1, 0, 0, 0, 0, 0, 0]
#L3  = [0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
L3  = [0, 1, 0, 0, 0, 0, 1, 0, 0, 0]
L4  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L5  = [0, 0, 0, 0, 0, 1, 1, 0, 0, 0]
L6  = [0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
L7  = [0, 0, 0, 0, 1, 1, 1, 1, 1, 1]
L8  = [0, 0, 0, 0, 0, 0, 1, 0, 1, 0]
L9  = [0, 0, 0, 0, 0, 0, 1, 0, 0, 1]
L10 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

L = np.array([L1, L2, L3, L4, L5, L6, L7, L8, L9, L10])

ITERATIONS = 100

def postprocess(result):
    indexes = []
    for i in range(len(result)):
        indexes.append(i + 1)
    result /= np.sum(result)
    result = np.transpose(result)
    result = list(zip(result[0], indexes))
    result.sort(key=lambda x: x[0], reverse=True)
    for i in result:
        print(i[1], ": ", i[0])
    return result

def getM(L):
    M = np.zeros([10, 10], dtype=float)
    # number of outgoing links
    c = np.zeros([10], dtype=int)
    
    ## TODO 1 compute the stochastic matrix M
    for i in range(0, 10):
        c[i] = sum(L[i])
    
    for i in range(0, 10):
        for j in range(0, 10):
            if L[j][i] == 0: 
                M[i][j] = 0
            else:
                M[i][j] = 1.0/c[j]
    return M

### TODO 2: compute pagerank with damping factor q = 0.15
### Then, sort and print: (page index (first index = 1 add +1) : pagerank)
### (use regular array + sort method + lambda function)
def computePagerank(numOfIterations, M):
    q = 0.15
    v = np.array([[0.1], [0.1], [0.1], [0.1], [0.1], [0.1], [0.1], [0.1], [0.1], [0.1]])
    for i in range(numOfIterations):
        v = q + (1 - q)*np.dot(M, v)
    return postprocess(v)


### TODO 3: compute trustrank with damping factor q = 0.15
### Documents that are good = 1, 2 (indexes = 0, 1)
### Then, sort and print: (page index (first index = 1, add +1) : trustrank)
### (use regular array + sort method + lambda function)

def computeTrustrank(numOfIterations, M):
    q = 0.15
    v = np.array([[0.5], [0.5], [0], [0], [0], [0], [0], [0], [0], [0]])
    d = np.array([[0.5], [0.5], [0], [0], [0], [0], [0], [0], [0], [0]])
    for i in range(numOfIterations):
        v = d*q + (1 - q) * np.dot(M, v)
    return postprocess(v)


print("Matrix L (indices)")
print(L)

M = getM(L)

print("Matrix M (stochastic matrix)")
print(M)

print("PAGERANK")
pr = computePagerank(ITERATIONS, M)
print(pr)

print("TRUSTRANK (DOCUMENTS 1 AND 2 ARE GOOD)")
tr = computeTrustrank(ITERATIONS, M)
print(tr)

    
### TODO 4: Repeat TODO 3 but remove the connections 3->7 and 1->5 (indexes: 2->6, 0->4) 
### before computing trustrank

L[0][4] = L[2][6] = 0
M = getM(L)
tr = computeTrustrank(ITERATIONS, M)
print(tr)