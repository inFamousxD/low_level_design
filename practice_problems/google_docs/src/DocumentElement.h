//
// Created by Aaditya on 2/9/26.
//

#ifndef LOW_LEVEL_DESIGN_DOCUMENTELEMENT_H
#define LOW_LEVEL_DESIGN_DOCUMENTELEMENT_H

#include "string"
#include <vector>

using namespace std;

class DocumentElement {
    public:
    virtual string render() = 0;

    virtual ~DocumentElement() = default;
};


#endif //LOW_LEVEL_DESIGN_DOCUMENTELEMENT_H