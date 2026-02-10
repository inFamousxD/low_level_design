//
// Created by Aaditya on 2/9/26.
//

#ifndef LOW_LEVEL_DESIGN_TABSPACEELEMENT_H
#define LOW_LEVEL_DESIGN_TABSPACEELEMENT_H
#include <string>

#include "DocumentElement.h"


class TabSpaceElement: public DocumentElement {
public:
    std::string render() override;
};


#endif //LOW_LEVEL_DESIGN_TABSPACEELEMENT_H