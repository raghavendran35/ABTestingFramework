'use strict';

// Register `modelList` component, along with its associated controller and template
angular.
  module('models').
  component('modelList', {
    template:
        '<ul>' +
          '<li ng-repeat="model in $ctrl.models">' +
            '<span><b>{{model.name}}</b></span>' +
            '<p>{{model.snippet}}</p>' +
          '</li>' +
        '</ul>',
    controller: function ModelListController() {
      this.models=[
    {
      name: '1: YOLO',
      snippet: 'we frame object detection as a re- gression problem to spatially separated bounding boxes and associated class probabilities. A single neural network pre- dicts bounding boxes and class probabilities directly from full images in one evaluation'
    },
    {
      name: '2: NASnet',
      snippet: "The goal of NAS is to use a data-driven and intelligent approach to constructing the network architecture instead of intuition and experiments. "
    },
    {
      name: '3: ResNet',
      snippet: "The ResNet architecture was developed by Kaiming He et al. in an attempt to train networks with even larger depth. The authors noted that increasing the network depth resulted in a higher training loss, indicating potential training convergence issues due to gradient problems (exploding/vanishing gradients)."
    },
    {
      name: '4: VGGNet',
      snippet: "Originally published in 2014 by Karen Simonyan and Andrew Zisserman, VGGNet showed that stacking multiple layers is a critical component for good performance in computer vision. Their published networks contain 16 or 19 layers and consist primarily of small 3x3 convolutions and 2x2 pooling operations."

    },
    {
      name: '5: Inceptionv3',
      snippet: "In Inceptionv3, the primary focus is on reusing some of the original ideas GoogLeNet and VGGNet, i.e. using the Inception module and expressing large filters more efficiently with a series of smaller convolutions. In addition to small convolutions, the authors also experiment with assymmetric convolutions (e.g. replacing nxn by nx1 and 1xn instead of multiple 2x2 and 3x3 filters)."

    }, 
    {
      name: '6: GoogLeNet',
      snippet: "With GoogLeNet however, the authors still attempt to scale up networks (up to 22 layers) but at the same time they aim to reduce the number of parameters and required computational power. "

    }

  ];
    }
  });