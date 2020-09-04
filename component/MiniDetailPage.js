import React, {Component} from 'react';
import {
    FlatList,
    StyleSheet,
    Text,
    View,
    NativeModules,
    Platform, // 判断当前运行的系统
    Navigator,
} from 'react-native';
import TextCell from './cell/TextCell.js';
// Component是一切界面组件的根类,实现界面组件必须要像继承此类
class MiniDetailPage extends Component {
    constructor() {
        super();
        this.state = {

        };
    }
    render() {
        return (
            <Text>
                似懂非懂舒服
            </Text>
        );
    }
}

// 输出组件类
export default MiniDetailPage;
