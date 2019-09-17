import React, { Fragment } from 'react';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Card, Form, Input, Row, Col, Select, Button, Table, Divider, Popconfirm, message, Popover } from 'antd';
import { connect } from 'dva';
import { auth } from '@/services/auth';
import moment from 'moment';
import styles from '../../index.less';

const FormItem = Form.Item;
const { Option } = Select;
// let obj = {};
class Role extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchObj: {},
    };
  }
  // --------------------生命周期 --------------------------------------------------
  componentDidMount() {
    const { list } = this.props;
    if (Object.getOwnPropertyNames(list).length === 0 || list.pageSize == 100) {
      this.onFetchList();
    }
    // this.setState({
    //   searchObj: obj
    // }, () => {
    //   if (Object.getOwnPropertyNames(list).length === 0 || list.pageSize == 100) {
    //     this.onFetchList();
    //   }
    // })
  }
  componentWillUnmount() {
    // const { searchObj } = this.state;
    // obj = searchObj;
  }
  // --------------------方法 --------------------------------------------------
  //列表
  onFetchList = (payload) => {
    const { searchObj } = this.state;
    const { dispatch } = this.props;
    let params = payload ? payload : searchObj;
    dispatch({
      type: 'auth/roleList',
      payload: params
    })
  }
  //查询
  onSubmit = (e) => {
    const { searchObj } = this.state;
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let params = { ...searchObj, ...values };
        this.setState({
          searchObj: params
        })
        this.onFetchList({ ...params, pageNo: 1 })
      }
    });
  };
  //分页,筛选,排序
  onTableChange = (pagination, filters, sorter) => {
    const { current, pageSize } = pagination;
    const { order, field } = sorter;
    const { searchObj } = this.state;
    let sort = order ? {
      sortValue: order === 'ascend' ? 'asc' : order === 'descend' ? 'desc' : '',
      sortName: field ? field : ''
    } : {};
    let params = {
      ...searchObj,
      ...sort,
      pageNo: pageSize === searchObj.pageSize ? parseInt(current) : 1,
      pageSize: parseInt(pageSize),
    };
    this.setState({
      searchObj: params
    }, () => {
      this.onFetchList({ ...params });
    });
  };
  //重置
  onReset = () => {
    const { searchObj } = this.state;
    const { resetFields } = this.props.form;
    resetFields();
    this.setState({
      searchObj: {}
    })
  }
  //编辑or添加
  onEdit = (record) => {
    let query = record ? { id: record.roleId } : {};
    this.props.history.push({
      pathname: '/auth/role/edit',
      query
    });
  };
  //删除
  onDelete = (roleId) => {
    const { searchObj } = this.state;
    auth.roleDelete({ roleId })
      .then((res) => {
        if (res && res.code == '1') {
          message.success('删除成功');
          this.onFetchList({
            ...searchObj,
            pageNo: 1
          });
        }
      })
  }
  // --------------------渲染 --------------------------------------------------
  renderSearch() {
    const { searchObj } = this.state;
    const { getFieldDecorator } = this.props.form;
    return (
      <Fragment>
        <Form className={styles.antAdvancedSearchForm} onSubmit={this.onSubmit}>
          <Row>
            <Col span={8}>
              <FormItem label="角色名称">
                {getFieldDecorator('roleName', {
                  initialValue: searchObj.roleName,
                })(<Input placeholder="请输入角色名称" className={styles.inputWidth} />)}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem label=" " colon={false}>
                <Button type="primary" htmlType="submit" style={{ marginRight: 10 }}>
                  查询
                </Button>
                <Button onClick={this.onReset}>重置</Button>
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Fragment>
    )
  }
  renderOpera() {
    return (
      <Fragment>
        <Button type="primary" onClick={() => this.onEdit()}>
          添加角色
        </Button>
      </Fragment>
    )
  }
  renderTable() {
    const { list, loading } = this.props;
    const columns = [
      {
        title: '角色名称',
        dataIndex: 'roleName',
        width: 150
      },
      {
        title: '描述',
        dataIndex: 'remark',
        className: styles.textOver,
        align: 'left',
      },
    ];
    return (
      <Fragment>
        <div>
          <Table
            // rowSelection={rowSelection}
            columns={columns}
            dataSource={list.rows || []}
            onChange={this.onTableChange}
            loading={loading}
            // pagination={{
            //   // showQuickJumper: true,
            //   pageSizeOptions: ['10', '20', '30', '50'],
            //   total: list.totalCount || 0,
            //   showTotal: (total, range) =>
            //     `共${list.totalCount || 0}条，当前${list.pageNum ? list.pageNum : 1}/${
            //     list.totalPage ? list.totalPage : 1
            //     }页`,
            //   showSizeChanger: true,
            //   current: list.pageNum ? list.pageNum : 1,
            //   pageSize: list.pageSize ? list.pageSize : 10,
            // }}
            pagination={false}
            rowKey={record => {
              return record.roleId;
            }}
          />
        </div>
      </Fragment>
    )
  }
  render() {
    return (
      <Card title="角色" bordered={false}>
        {/* {this.renderOpera()} */}
        {this.renderTable()}
      </Card>
    );
  }
}
export default Form.create()(connect(({ auth, loading }) => ({
  list: auth.roleList,
  loading: loading.effects["auth/roleList"]
}))(Role));
